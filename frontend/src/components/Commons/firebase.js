import firebase from 'firebase/compat/app';
import 'firebase/compat/messaging';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
    apiKey: 'AIzaSyA4Ix8zhhpUXEbKeOZqIL93Z9OiQqxvpWM',
    authDomain: 'spring-boot-firestore-f843c.firebaseapp.com',
    projectId: 'spring-boot-firestore-f843c',
    storageBucket: 'spring-boot-firestore-f843c.firebasestorage.app',
    messagingSenderId: '1028714895005',
    appId: '1:1028714895005:web:6131f18c0a0c74c854a674',
    measurementId: 'G-EH58RCZ64W',
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);

// Initialize Firebase Cloud Messaging and get a reference to the service
const firebaseMessaging = firebase.messaging();

firebaseMessaging
    .getToken({ vapidKey: process.env.FIREBASE_KEY_PAIR })
    .then((currentToken) => {
        if (currentToken) {
            fetch(`${process.env.REACT_APP_API_URL}/notification/fcm/token`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    token: currentToken,
                }),
            }).then((response) => {
                if (response.ok) {
                    console.log('Token sent to the server');
                } else {
                    console.log('Token not sent to the server');
                }
            });
        } else {
            // Show permission request UI
            console.log('No registration token available. Request permission to generate one.');
            // ...
        }
    })
    .catch((err) => {
        console.log('An error occurred while retrieving token. ', err);
        // ...
    });
const firebaseAuth = getAuth(firebase.app());
export { firebase as FirebaseApp, firebaseMessaging, firebaseAuth };
