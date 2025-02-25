import { useState, useEffect } from 'react';
import { getUserInfoRequest } from '../../components/Commons/auth';
import { Link } from 'react-router-dom';

export default function UserProfile() {
    const [currentUser, setCurrentUser] = useState(null);
    useEffect(() => {
        const getUserInfo = async () => {
            const userInfo = await getUserInfoRequest();
            if (userInfo && userInfo.statusCode == 200) {
                setCurrentUser(userInfo.data);
            }
        };
        getUserInfo();
    }, []);
    if (!currentUser) return <div className="flex justify-center items-center h-screen">Loading ..</div>;
    return (
        <>
            <div className="w-[80vw] mx-auto">
                <h1 className="text-center font-semibold text-3xl my-6">My profile</h1>
                <section className="w-full overflow-hidden dark:bg-gray-900">
                    <div className="flex flex-col">
                        <img
                            src={
                                'https://img.freepik.com/free-photo/modern-futuristic-sci-fi-background_35913-2150.jpg?t=st=1740366887~exp=1740370487~hmac=b679dff9daa3e307ffb0bf842fdd710dfa13dd33ce1125063cb2a31565c0befa&w=1380'
                            }
                            alt="User Cover"
                            width={0}
                            height={100}
                            className="w-full xl:h-[20rem] lg:h-[18rem] md:h-[16rem] sm:h-[14rem] xs:h-[11rem] rounded-lg"
                        />

                        <div className="sm:w-[80%] xs:w-[90%] mx-auto flex">
                            <div className="h-[7rem] w-[7rem] md:h-[7rem] md:w-[7rem] lg:h-[10rem] lg:w-[10rem] avatar-profile border-white rounded-full overflow-hidden -mt-10">
                                <img
                                    src={
                                        currentUser?.photoUrl ||
                                        'https://www.shutterstock.com/image-vector/cute-cat-wear-dino-costume-600nw-2457633459.jpg'
                                    }
                                    alt="User Profile"
                                    className="w-full h-full object-cover"
                                />
                            </div>

                            <h1 className="text-left my-4 sm:mx-4 xs:pl-4 text-gray-800 dark:text-white lg:text-4xl md:text-3xl text-3xl font-serif min-w-60">
                                {currentUser.displayName}
                            </h1>
                        </div>

                        <div className="xl:w-[80%] lg:w-[90%] md:w-[90%] sm:w-[92%] xs:w-[90%] mx-auto flex flex-col gap-4 items-center relative ">
                            <div className="w-full my-auto pb-6 flex flex-col justify-center gap-2">
                                <div className="w-full flex sm:flex-row xs:flex-col gap-2 justify-center">
                                    <div className="w-full">
                                        <dl className="text-gray-900 divide-y divide-gray-200 dark:text-white dark:divide-gray-700">
                                            <div className="flex flex-col pt-3">
                                                <dt className="mb-1 text-gray-500 md:text-lg dark:text-gray-400">
                                                    Email
                                                </dt>
                                                <dd className="text-lg font-semibold">{currentUser.email}</dd>
                                            </div>
                                            <div className="flex flex-col pb-3">
                                                <dt className="mb-1 text-gray-500 md:text-lg dark:text-gray-400">Id</dt>
                                                <dd className="text-lg font-semibold">{currentUser.userId}</dd>
                                            </div>
                                        </dl>
                                    </div>
                                    <div className="w-full">
                                        <dl className="text-gray-900 divide-y divide-gray-200 dark:text-white dark:divide-gray-700">
                                            <div className="flex flex-col pt-3">
                                                <dt className="mb-1 text-gray-500 md:text-lg dark:text-gray-400">
                                                    Phone number
                                                </dt>
                                                <dd className="text-lg font-semibold">{currentUser.phoneNumber}</dd>
                                            </div>
                                            {/* <div className="flex flex-col pt-3">
                                            <dt className="mb-1 text-gray-500 md:text-lg dark:text-gray-400">Role:</dt>
                                            <span className="text-lg font-semibold">{currentUser.role}</span>
                                        </div> */}
                                        </dl>
                                    </div>
                                </div>
                            </div>
                            <div className="flex justify-between items-center gap-5 pb-5">
                                <button className="bg-violet-500 hover:bg-violet-700 text-white px-3 py-1 rounded-md">
                                    <Link to="/">Back home</Link>
                                </button>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </>
    );
}
