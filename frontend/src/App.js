import { Fragment } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import DefaultLayout from './layouts/DefaultLayout';
import { publicRoutes as routes } from './routes';
import { firebaseMessaging } from './components/Commons/firebase';
import { useEffect } from 'react';
import { useState } from 'react';

export default function App() {
    const [notification, setNotification] = useState(null);
    useEffect(() => {
        navigator.serviceWorker
            .register('/firebase-messaging-sw.js')
            .then((registration) => {
                navigator.serviceWorker.addEventListener('message', (event) => {
                    const payload = event.data;
                    setNotification({
                        title: payload.notification.title,
                        body: payload.notification.body,
                        type: payload.data.type,
                    });
                });
            })
            .catch((error) => {
                console.error('Service Worker registration failed:', error);
            });
    }, []);
    return (
        <>
            {notification && (
                <div className="flex top-0 bottom-0 left-0 right-0 items-center justify-center fixed z-[9999] bg-black bg-opacity-75">
                    <div className="min-w-[300px] min-h-[100px] rounded-lg shadow-lg p-4 flex flex-col justify-between text-center gap-3 z-[99999] bg-white">
                        <div className="text-xl font-semibold">{notification.title}</div>
                        <div className="text-gray-400 text-lg">{notification.body}</div>
                        <button
                            onClick={() => window.location.reload()}
                            className="bg-purple-500 hover:bg-purple-700 text-white font-bold py-2 px-4 rounded"
                        >
                            Reload
                        </button>
                    </div>
                </div>
            )}
            <BrowserRouter>
                <Routes>
                    {routes.map((route, index) => {
                        const Component = route.component;
                        let Layout = DefaultLayout;
                        if (route?.layout) {
                            Layout = route.layout;
                        } else if (route.layout === null) {
                            Layout = Fragment;
                        }
                        const Page = (
                            <Layout>
                                <Component />
                            </Layout>
                        );
                        return <Route key={index} path={route.path} element={Page} />;
                    })}
                </Routes>
            </BrowserRouter>
        </>
    );
}
