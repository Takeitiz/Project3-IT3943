import { FC, FormEvent, ReactElement, useEffect, useState } from 'react';
import Breadcrumb from '../../../../shared/breadcrumb/Breadcrumb.tsx';
import { IReduxState } from '../../../../store/store.interface.ts';
import { useAppDispatch, useAppSelector } from '../../../../store/store.ts';
import CircularPageLoader from '../../../../shared/page-loader/CircularPageLoader.tsx';
import PersonalInfo from './components/PersonalInfo.tsx';
import {
  ICertificate,
  IEducation,
  IExperience,
  ILanguage,
  IPersonalInfoData, ISellerDocument
} from '../../interfaces/seller.interface.ts';
import SellerExperienceFields from './components/SellerExperienceFields.tsx';
import SellerEducationFields from './components/SellerEducationFields.tsx';
import SellerSkillField from './components/SellerSkillField.tsx';
import SellerLanguageFields from './components/SellerLanguageFields.tsx';
import SellerCertificateFields from './components/SellerCertificateFields.tsx';
import SellerSocialLinksFields from './components/SellerSocialLinksFields.tsx';
import { useSellerSchema } from '../../hooks/useSellerSchema.ts';
import Button from '../../../../shared/button/Button.tsx';
import { filter } from 'lodash';
import { IBuyerDocument } from '../../../buyer/interfaces/buyer.interface.ts';
import { IResponse } from '../../../../shared/shared.interface.ts';
import { addSeller } from '../../reducers/seller.reducer.ts';
import { addBuyer } from '../../../buyer/reducers/buyer.reducer.ts';
import { deleteFromLocalStorage, lowerCase } from '../../../../shared/utils/utils.service.ts';
import { useCreateSellerMutation } from '../../services/seller.service.ts';
import { NavigateFunction, useNavigate } from 'react-router-dom';

const AddSeller: FC = ():ReactElement => {
  const authUser = useAppSelector((state: IReduxState) => state.authUser);
  const buyer = useAppSelector((state: IReduxState) => state.buyer);
  const [personalInfo, setPersonalInfo] = useState<IPersonalInfoData>({
    fullName: '',
    profilePicture: `${authUser.profilePicture}`,
    description: '',
    responseTime: '',
    oneliner: ''
  });
  const [experienceFields, setExperienceFields] = useState<IExperience[]>([
    {
      title: '',
      company: '',
      startDate: 'Start Year',
      endDate: 'End Year',
      currentlyWorkingHere: false,
      description: ''
    }
  ]);
  const [educationFields, setEducationFields] = useState<IEducation[]>([
    {
      country: 'Country',
      university: '',
      title: 'Title',
      major: '',
      year: 'Year'
    }
  ]);
  const [skillsFields, setSkillsFields] = useState<string[]>(['']);
  const [languageFields, setLanguageFields] = useState<ILanguage[]>([
    {
      language: '',
      level: 'Level'
    }
  ]);
  const [certificateFields, setCertificateFields] = useState<ICertificate[]>([
    {
      name: '',
      from: '',
      year: 'Year'
    }
  ]);
  const [socialFields, setSocialFields] = useState<string[]>(['']);
  const [schemaValidation, personalInfoErrors, experienceErrors, educationErrors, skillsErrors, languagesErrors] = useSellerSchema({
    personalInfo,
    experienceFields,
    educationFields,
    skillsFields,
    languageFields
  });
  const dispatch = useAppDispatch();
  const navigate: NavigateFunction = useNavigate();
  const [createSeller, { isLoading }] = useCreateSellerMutation();

  const errors = [...personalInfoErrors, ...experienceErrors, ...educationErrors, ...skillsErrors, ...languagesErrors];

  const onCreateSeller = async (event: FormEvent): Promise<void> => {
    event.preventDefault();
    try {
      const isValid: boolean = await schemaValidation();
      if (isValid) {
        const skills: string[] = filter(skillsFields, (skill: string) => skill !== '') as string[];
        const socialLinks: string[] = filter(socialFields, (item: string) => item !== '') as string[];
        const certificates: ICertificate[] = filter(
          certificateFields,
          (item: ICertificate) => item.name !== '' && item.from !== '' && item.year !== ''
        ) as ICertificate[];
        const sellerData: ISellerDocument = {
          email: `${authUser.email}`,
          profilePublicId: `${authUser.profilePublicId}`,
          profilePicture: `${authUser.profilePicture}`,
          username: `${authUser.username}`,
          fullName: personalInfo.fullName,
          description: personalInfo.description,
          country: `${authUser.country}`,
          skills,
          oneliner: personalInfo.oneliner,
          languages: languageFields,
          responseTime: parseInt(personalInfo.responseTime, 10),
          experience: experienceFields,
          education: educationFields,
          socialLinks,
          certificates
        };
        const updateBuyer: IBuyerDocument = { ...buyer, isSeller: true };
        const response: IResponse = await createSeller(sellerData).unwrap();
        dispatch(addSeller(response.seller));
        dispatch(addBuyer(updateBuyer));
        navigate(`/seller_profile/${lowerCase(`${authUser.username}`)}/${response.seller?.id}/edit`);
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    return () => {
      // delete becomeASeller from localStorage when user leaves this page
      deleteFromLocalStorage('becomeASeller');
    };
  }, []);


  return (
    <div className="relative w-full">
      <Breadcrumb breadCrumbItems={['Seller', 'Create Profile']} />
      <div className="container mx-auto my-5 overflow-hidden px-2 pb-12 md:px-0">
        {isLoading && <CircularPageLoader />}
        {authUser && !authUser.emailVerified && (
          <div className="absolute left-0 top-0 z-50 flex h-full w-full justify-center bg-white/[0.8] text-sm font-bold md:text-base lg:text-xl">
            <span className="mt-20">Please verify your email.</span>
          </div>
        )}

        <div className="left-0 top-0 z-10 mt-4 block h-full bg-white">
          {errors.length > 0 ? (
            <div className="text-red-400">{`You have ${errors.length} error${errors.length > 1 ? 's' : ''}`}</div>
          ) : (
            <></>
          )}
          <PersonalInfo personalInfo={personalInfo} setPersonalInfo={setPersonalInfo}
                        personalInfoErrors={personalInfoErrors} />
          <SellerExperienceFields
            experienceFields={experienceFields}
            setExperienceFields={setExperienceFields}
            experienceErrors={experienceErrors}
          />
          <SellerEducationFields
            educationFields={educationFields}
            setEducationFields={setEducationFields}
            educationErrors={educationErrors}
          />
          <SellerSkillField skillsFields={skillsFields} setSkillsFields={setSkillsFields} skillsErrors={skillsErrors} />
          <SellerLanguageFields languageFields={languageFields} setLanguageFields={setLanguageFields}
                                languagesErrors={languagesErrors} />
          <SellerCertificateFields certificatesFields={certificateFields}
                                   setCertificatesFields={setCertificateFields} />
          <SellerSocialLinksFields socialFields={socialFields} setSocialFields={setSocialFields} />
          <div className="flex justify-end p-6">
            <Button
              onClick={onCreateSeller}
              className="rounded bg-sky-500 px-8 text-center text-sm font-bold text-white hover:bg-sky-400 focus:outline-none md:py-3 md:text-base"
              label="Create Profile"
            />
          </div>
        </div>
      </div>
    </div>
  )
}

export default AddSeller
