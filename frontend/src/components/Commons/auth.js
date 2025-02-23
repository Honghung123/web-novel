import { firebaseAuth } from './firebase';
import {
    createUserWithEmailAndPassword,
    signInWithEmailAndPassword,
    sendPasswordResetEmail,
    sendEmailVerification,
    updatePassword,
    signInWithPopup,
    GoogleAuthProvider,
} from 'firebase/auth';

export const doCreateUserWithEmailAndPassword = async (email, password) => {
    return createUserWithEmailAndPassword(auth, email, password);
};

export const doSignInWithEmailAndPassword = (email, password) => {
    return signInWithEmailAndPassword(auth, email, password);
};

export const doSignInWithGoogle = async () => {
    const provider = new GoogleAuthProvider();
    const result = await signInWithPopup(firebaseAuth, provider);
    const user = result.user;
    console.log(user);
    // add user to firestore
};

export const doSignOut = () => {
    return firebaseAuth.signOut();
};

export const doPasswordReset = (email) => {
    return sendPasswordResetEmail(firebaseAuth, email);
};

export const doPasswordChange = (password) => {
    return updatePassword(firebaseAuth.currentUser, password);
};

export const doSendEmailVerification = () => {
    return sendEmailVerification(firebaseAuth.currentUser, {
        url: `${window.location.origin}/home`,
    });
};
