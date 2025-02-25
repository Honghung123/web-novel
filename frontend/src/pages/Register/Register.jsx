import React, { useState } from 'react';
import { Navigate, Link } from 'react-router-dom';
import { doRegisterWithEmailAndPassword } from './../../components/Commons/auth';

const Register = () => {
    const [phoneNumber, setPhoneNumber] = useState('0846846222');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [displayName, setDisplayName] = useState('Hong Hung');
    const [isSigningIn, setIsSigningIn] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const onSubmit = async (e) => {
        e.preventDefault();
        if (!isSigningIn) {
            setIsSigningIn(true);
            const result = await doRegisterWithEmailAndPassword({ email, password, displayName, phoneNumber });
            setIsSigningIn(false);
            if (result && result.statusCode == 200) {
                window.location.href = '/login';
            }
        }
    };
    return (
        <div>
            <main className="w-full h-screen flex self-center place-content-center place-items-center">
                <div className="w-96 text-gray-600 space-y-5 p-4 shadow-xl border rounded-xl">
                    <div className="text-center">
                        <div className="mt-2">
                            <h3 className="text-gray-800 text-xl font-semibold sm:text-2xl">Register</h3>
                        </div>
                    </div>
                    <form onSubmit={onSubmit} className="space-y-2">
                        <div>
                            <label className="text-sm text-gray-600 font-bold">Name</label>
                            <input
                                required
                                value={displayName}
                                onChange={(e) => {
                                    setDisplayName(e.target.value);
                                }}
                                className="w-full mt-2 px-3 py-2 text-gray-500 bg-transparent outline-none border focus:border-indigo-600 shadow-sm rounded-lg transition duration-300"
                            />
                        </div>
                        <div>
                            <label className="text-sm text-gray-600 font-bold">Phone number</label>
                            <input
                                required
                                value={phoneNumber}
                                onChange={(e) => {
                                    setPhoneNumber(e.target.value);
                                }}
                                className="w-full mt-2 px-3 py-2 text-gray-500 bg-transparent outline-none border focus:border-indigo-600 shadow-sm rounded-lg transition duration-300"
                            />
                        </div>
                        <div>
                            <label className="text-sm text-gray-600 font-bold">Email</label>
                            <input
                                type="email"
                                autoComplete="email"
                                required
                                value={email}
                                onChange={(e) => {
                                    setEmail(e.target.value);
                                }}
                                className="w-full mt-2 px-3 py-2 text-gray-500 bg-transparent outline-none border focus:border-indigo-600 shadow-sm rounded-lg transition duration-300"
                            />
                        </div>

                        <div>
                            <label className="text-sm text-gray-600 font-bold">Password</label>
                            <input
                                type="password"
                                autoComplete="current-password"
                                required
                                value={password}
                                onChange={(e) => {
                                    setPassword(e.target.value);
                                }}
                                className="w-full mt-2 px-3 py-2 text-gray-500 bg-transparent outline-none border focus:border-indigo-600 shadow-sm rounded-lg transition duration-300"
                            />
                        </div>

                        {errorMessage && <span className="text-red-600 font-bold">{errorMessage}</span>}

                        <div className="flex items-center pt-3 justify-center">
                            <button
                                type="submit"
                                disabled={isSigningIn}
                                className={`px-4 py-2 text-white font-medium rounded-lg ${
                                    isSigningIn
                                        ? 'bg-gray-300 cursor-not-allowed'
                                        : 'bg-indigo-600 hover:bg-indigo-700 hover:shadow-xl transition duration-300'
                                }`}
                            >
                                {isSigningIn ? 'Signing In...' : 'Sign In'}
                            </button>
                        </div>
                    </form>
                    <p className="text-center text-sm">
                        Already had an account?{' '}
                        <Link to={'/login'} className="hover:underline font-bold">
                            Sign in
                        </Link>
                    </p>
                    {/* <div className="flex flex-row text-center w-full">
                        <div className="border-b-2 mb-2.5 mr-2 w-full"></div>
                        <div className="text-sm font-bold w-fit">OR</div>
                        <div className="border-b-2 mb-2.5 ml-2 w-full"></div>
                    </div> */}
                </div>
            </main>
        </div>
    );
};

export default Register;
