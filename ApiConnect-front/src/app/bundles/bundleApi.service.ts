import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {Org } from './org';

const API = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class BundleApiService {

  constructor(private httpClient: HttpClient) { }

  recalcParams = new HttpParams().set('recalc', "true");

  public getAllOrgs(recalc: boolean):Observable<Org[]>{
    if (recalc){
      return this.httpClient.get<Org[]>(API+'/orgs/',{params : this.recalcParams});
    }
    else return this.httpClient.get<Org[]>(API+'/orgs/');
  }

  public getOrg(id: number, recalc: boolean):Observable<Org>{
    if (recalc){
      return this.httpClient.get<Org>(API+'/orgs/' + id,{params : this.recalcParams});
    }
    else return this.httpClient.get<Org>(API+'/orgs/' + id);
  }

  public getgetOrgWithTickets(id: number, recalc: boolean):Observable<Org>{
    if (recalc){
      return this.httpClient.get<Org>(API+'/orgs/' + id + "/tickets",{params : this.recalcParams});
    }
    else return this.httpClient.get<Org>(API+'/orgs/' + id + "/tickets");
  }


}
