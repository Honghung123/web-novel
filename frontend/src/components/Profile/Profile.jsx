import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getUserInfoRequest, doSignOut } from '../../components/Commons/auth';

export default function Profile() {
    const [user, setUser] = useState(null);
    const [open, setOpen] = useState(false);
    useEffect(() => {
        const getUserInfo = async () => {
            const userInfo = await getUserInfoRequest();
            if (userInfo && userInfo.statusCode == 200) {
                setUser(userInfo.data);
            }
        };
        getUserInfo();
    }, []);
    const handleLogoutUser = () => {
        doSignOut();
        localStorage.removeItem('idToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
    };
    return (
        <>
            {user ? (
                <>
                    <div
                        className="flex items-center gap-1 text-sky-600 relative text-left cursor-pointer"
                        onClick={() => setOpen(!open)}
                    >
                        <p className="text-end text-sm">{user.displayName || user.email}</p>
                        <img
                            src={
                                user.photoUrl ||
                                'https://www.shutterstock.com/image-vector/cute-cat-wear-dino-costume-600nw-2457633459.jpg'
                            }
                            alt={user.displayName || user.email}
                            className="w-10 h-10 rounded-full cursor-pointer border border-white"
                        />
                    </div>
                    {open && (
                        <>
                            <div className="absolute w-40 top-16 right-2 rounded-md bg-white ring-1 shadow-lg ring-black/5 focus:outline-hidden">
                                <div className="py-1" role="none">
                                    <Link
                                        to="/profile"
                                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                                    >
                                        Profile
                                    </Link>
                                    <button
                                        className="block w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100"
                                        onClick={handleLogoutUser}
                                    >
                                        Sign out
                                    </button>
                                </div>
                            </div>
                        </>
                    )}
                </>
            ) : (
                <>
                    <div className="flex items-center gap-3">
                        <Link to={'/login'}>
                            <button className="bg-violet-500 text-white py-2 px-4 rounded">Login</button>
                        </Link>

                        <Link to={'/register'}>
                            <button className="bg-violet-500 text-white py-2 px-4 rounded">Register</button>
                        </Link>
                    </div>
                </>
            )}
        </>
    );
}
