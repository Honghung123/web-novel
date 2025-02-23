// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here. Other Firebase libraries
// are not available in the service worker.
// Replace 10.13.2 with latest version of the Firebase JS SDK.
importScripts('https://www.gstatic.com/firebasejs/10.13.2/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.13.2/firebase-messaging-compat.js');

// Initialize the Firebase app in the service worker by passing in
// your app's Firebase config object.
// https://firebase.google.com/docs/web/setup#config-object
const firebaseConfig = {
    apiKey: 'AIzaSyA4Ix8zhhpUXEbKeOZqIL93Z9OiQqxvpWM',
    authDomain: 'spring-boot-firestore-f843c.firebaseapp.com',
    projectId: 'spring-boot-firestore-f843c',
    storageBucket: 'spring-boot-firestore-f843c.firebasestorage.app',
    messagingSenderId: '1028714895005',
    appId: '1:1028714895005:web:6131f18c0a0c74c854a674',
    measurementId: 'G-EH58RCZ64W',
};
firebase.initializeApp(firebaseConfig);

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);

    // Gửi payload vào luồng chính
    self.clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clients) => {
        clients.forEach((client) => {
            client.postMessage({
                type: 'NEW_MESSAGE',
                payload: payload,
            });
        });
    });
});
