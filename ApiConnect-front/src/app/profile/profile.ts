export class ApiConnection {
  id: number;
  name: string;
  baseURL: string;
  CredentialType: string;
  credKey: string;
  credValue: string;
  editMode: boolean;
  unsaved: boolean;
}

export class Profile {
  id: number;
  name: string;
  connections: ApiConnection[];
  editMode: boolean = false;
  unsaved: boolean;
}
