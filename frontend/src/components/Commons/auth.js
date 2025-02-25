import { firebaseAuth } from './firebase';
import {
    createUserWithEmailAndPassword,
    signInWithEmailAndPassword,
    sendPasswordResetEmail,
    sendEmailVerification,
    updatePassword,
    signInWithPopup,
    GoogleAuthProvider,
    GithubAuthProvider,
} from 'firebase/auth';
import axios from 'axios';

export const getUserInfoRequest = async () => {
    try {
        const idToken = localStorage.getItem('idToken');
        if (!idToken) return null;
        const result = await axios.get(`${process.env.REACT_APP_API_URL}/auth/info/${idToken}`);
        return result.data;
    } catch (error) {
        return null;
    }
};

export const doRegisterWithEmailAndPassword = async (payload) => {
    try {
        const result = await axios.post(`${process.env.REACT_APP_API_URL}/auth/register`, payload);
        return result.data;
    } catch (error) {
        return null;
    }
};

export const doSignInWithEmailAndPassword = async (payload) => {
    try {
        const body = { ...payload, returnSecureToken: true };
        const result = await axios.post(`${process.env.REACT_APP_API_URL}/auth/login`, body);
        return result.data;
    } catch (error) {
        return null;
    }
};

export const doSignInWithGoogle = async () => {
    const provider = new GoogleAuthProvider();
    const result = await signInWithPopup(firebaseAuth, provider);
    const user = result.user;
    // add user to firestore
    doRegisterWithEmailAndPassword({
        email: user.email,
        password: user.uid,
        phoneNumber: user.phoneNumber,
        displayName: user.displayName,
        photoUrl: user.photoURL,
        uid: user.uid,
    })
        .then(async (result) => {
            if (!result || result.statusCode != 200) return;
            const data = await doSignInWithEmailAndPassword({
                email: user.email,
                password: user.uid,
                returnSecureToken: true,
            });
            if (data && data.statusCode == 200) {
                localStorage.setItem('idToken', data.data.idToken);
                localStorage.setItem('refreshToken', data.data.refreshToken);
                window.location.href = '/';
            } else {
                throw new Error('Error to login with google');
            }
        })
        .catch((err) => {
            throw new Error('Error to login with google', err);
        });
};

export const doSignInWithGithub = async () => {
    const provider = new GithubAuthProvider();
    signInWithPopup(firebaseAuth, provider)
        .then(async (result) => {
            console.log(result);
            const user = result.user;
            console.log(user);
            // add user to firestore
            const registerResponse = await doRegisterWithEmailAndPassword({
                email: user.email,
                password: user.uid,
                phoneNumber: user.phoneNumber,
                displayName: user.displayName,
                photoUrl: user.photoURL,
                uid: user.uid,
            });
            if (!registerResponse || registerResponse.statusCode != 200) return;
            const data = await doSignInWithEmailAndPassword({
                email: user.email,
                password: user.uid,
                returnSecureToken: true,
            });
            if (data && data.statusCode == 200) {
                localStorage.setItem('idToken', data.data.idToken);
                localStorage.setItem('refreshToken', data.data.refreshToken);
                window.location.href = '/';
            } else {
                throw new Error('Error to login with github');
            }
        })
        .catch((error) => {
            throw new Error('Error to login with github', error);
        });
};

export const doSignOut = () => {
    try {
        firebaseAuth.signOut();
    } catch (error) {
        console.log('Error when sign out', error);
    }
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
