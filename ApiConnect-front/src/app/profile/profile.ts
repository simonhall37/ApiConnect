export class ApiConnection {
  id: number;
  name: string;
  baseURL: string;
  CredentialType: string;
  credKey: string;
  credValue: string;
}

export class Profile {
  id: number;
  name: string;
  connections: ApiConnection[];
}
