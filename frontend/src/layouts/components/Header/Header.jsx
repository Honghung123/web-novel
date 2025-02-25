import React from 'react';
import AutoStoriesIcon from '@mui/icons-material/AutoStories';
import { Link } from 'react-router-dom';

import SearchBox from '../../../components/SearchBox';
import Profile from '../../../components/Profile';

function Header() {
    return (
        <div
            className="flex justify-between items-center p-2"
            style={{
                background: 'linear-gradient(to right, rgba(155, 86, 244, 0.7), rgba(247, 162, 249, 0.5))',
            }}
        >
            <Link to={'/'} className="text-white flex items-center justify-center p-2 cursor-pointer">
                <AutoStoriesIcon sx={{ fontSize: 50 }} />
                <div className="text-3xl ml-4 hidden sm:block">WebComic</div>
            </Link>
            <SearchBox></SearchBox>
            <Profile />
        </div>
    );
}

export default Header;
