import { FC, lazy, LazyExoticComponent, ReactElement, Suspense, useEffect } from 'react';
import { IHeader } from '../../shared/header/interfaces/header.interface.ts';
import Hero from './Hero.tsx';
import HowItWorks from './HowItWorks.tsx';
import Categories from './Categories.tsx';
import { saveToSessionStorage } from '../../shared/utils/utils.service.ts';
import CircularPageLoader from '../../shared/page-loader/CircularPageLoader.tsx';

const IndexHeader: LazyExoticComponent<FC<IHeader>> = lazy(() => import('src/shared/header/components/Header'));


const Index: FC = (): ReactElement => {
  useEffect(() => {
    saveToSessionStorage(JSON.stringify(false), JSON.stringify(''));
  }, []);

  return (
    <div className="flex flex-col">
      <Suspense fallback={<CircularPageLoader />}>
        <IndexHeader navClass="navbar peer-checked:navbar-active fixed z-20 w-full border-b border-gray-100 bg-white/90 shadow-2xl shadow-gray-600/5 backdrop-blur dark:border-gray-800 dark:bg-gray-900/80 dark:shadow-none" />
        <Hero />
        <HowItWorks />
        <hr />
        <Categories />
      </Suspense>
    </div>
  );
}

export default Index
