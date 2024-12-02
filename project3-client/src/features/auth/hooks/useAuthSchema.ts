import { IUseAuthSchema } from '../interfaces/auth.interface.ts';
import { validationErrorsType } from '../../../shared/shared.interface.ts';
import { useState } from 'react';

const useAuthSchema = ({ schema, userInfo }: IUseAuthSchema): [() => Promise<boolean>, validationErrorsType[]] => {
  const [validationErrors, setValidationErrors] = useState<validationErrorsType[]>([]);

  async function schemaValidation(): Promise<boolean> {
    await schema
      .validate(userInfo, { abortEarly: false })
      .then(() => setValidationErrors([]))
      .catch((err) => {
        setValidationErrors([...err.errors]);
      });
    const validation: boolean = await schema.isValid(userInfo, { abortEarly: false });
    return validation;
  }
  return [schemaValidation, validationErrors];
};

export { useAuthSchema };
