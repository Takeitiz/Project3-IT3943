import { FC } from 'react';
import { IProfileHeaderProps, ISellerDocument } from '../../../interfaces/seller.interface.ts';
import { SellerContext } from '../../../context/SellerContext.ts';
import Language from './overview/language/Language.tsx';
import AboutMe from './overview/aboutme/AboutMe.tsx';
import SocialLinks from './overview/sociallinks/SocialLinks.tsx';
import Certifications from './overview/certifications/Certifications.tsx';
import Description from './overview/description/Description.tsx';
import Experience from './overview/experience/Experience.tsx';
import Education from './overview/education/Education.tsx';
import Skills from './overview/skills/Skills.tsx';


const SellerOverview: FC<IProfileHeaderProps> = ({ sellerProfile, setSellerProfile, showEditIcons }) => {
  return (
    <SellerContext.Provider
      value={{ showEditIcons, setSellerProfile, sellerProfile: sellerProfile as ISellerDocument }}>
      <div className="w-full py-4 lg:w-1/3">
        <Language />
        <AboutMe />
        <SocialLinks />
        <Certifications />
      </div>

      <div className="w-full pl-4 py-4 lg:w-2/3">
        <Description />
        <Experience />
        <Education />
        <Skills />
      </div>
    </SellerContext.Provider>
  );
};

export default SellerOverview;
