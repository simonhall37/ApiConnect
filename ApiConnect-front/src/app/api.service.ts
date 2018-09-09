import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import {Profile } from './profile/profile';

const API = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private httpClient: HttpClient) { }

  public getAllProfiles():Observable<Profile[]>{
    return this.httpClient.get<Profile[]>(API+'/profiles');
  }

  public putProfile(profile: Profile){
    return this.httpClient.put<Profile>(API+'/profiles/'+profile.name,profile);
  }

  public deleteProfile(profile: Profile){
    return this.httpClient.delete<Profile>(API + '/profiles/'+profile.name);
  }
}
