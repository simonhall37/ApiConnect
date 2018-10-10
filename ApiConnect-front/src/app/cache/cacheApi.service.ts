import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {CacheSummary } from './cacheSummary';

const API = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class CacheApiService {

  constructor(private httpClient: HttpClient) { }

  public getAllCacheSummaries():Observable<CacheSummary[]>{
    return this.httpClient.get<CacheSummary[]>(API+'/cache/summaries');
  }

  public putCacheSummary(summary: CacheSummary){
    return this.httpClient.put<CacheSummary>(API+'/cache/summaries/'+summary.name,summary);
  }

  public deleteCacheSummary(summary: CacheSummary){
    return this.httpClient.delete<CacheSummary>(API + '/cache/summaries/'+summary.name);
  }

  public postCacheSummary(summary: CacheSummary){
    return this.httpClient.post<CacheSummary>(API + '/cache/summaries/',summary);
  }

  public cache(summary: CacheSummary){
    return this.httpClient.post<CacheSummary>(API + '/cache/summaries/' + summary.name,summary);
  }

}
