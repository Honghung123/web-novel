import { useContext, useLayoutEffect } from 'react';

import { Context } from '../../components/GlobalContext';
import ComicSources from '../../components/ComicSources';
import ListComics from '../../components/ListComics';
import ListLastUpdated from '../../components/ListLastUpdated';
import ReadingHistory from '../../components/ReadingHistory';

function Home() {
    const { setCurrentPage } = useContext(Context);
    useLayoutEffect(() => setCurrentPage(''), []);

    return (
        <div className="px-4 py-2">
            <div className="flex flex-wrap mx-auto max-w-[1200px]">
                <div className="flex-1">
                    <ListComics />
                </div>
                <div className="w-full sm:w-4/12 lg:w-3/12 ml-4">
                    <div className="py-4">
                        <div className="mx-auto pt-4 mt-8 w-full sm:max-w-64 max-w-96">
                            <ComicSources />
                        </div>
                        <div className="w-full sm:max-w-64 max-w-96 mt-4 mx-auto">
                            <ReadingHistory />
                        </div>
                    </div>
                </div>
            </div>
            <ListLastUpdated />
        </div>
    );
}

export default Home;
