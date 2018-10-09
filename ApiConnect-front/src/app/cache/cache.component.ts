import { Component, OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import {CacheSummary, Pair} from './cacheSummary';
import {Filter, LookupFilter, TextFilter} from './filter';
import {CacheApiService} from './cacheApi.service';
import { HttpErrorResponse } from '@angular/common/http';

class Message {
  type: string;
  show: boolean;
  content: string;
}

@Component({
  selector: 'af-cache',
  templateUrl: './cache.component.html',
  styleUrls: ['./cache.component.scss'],

})
export class CacheComponent implements OnInit  {

  message: Message;
  summaries: CacheSummary[] = [];
  newName: string;

  constructor(private cacheApiService: CacheApiService) {}

    ngOnInit() {
      this.cacheApiService.getAllCacheSummaries().subscribe(
        (summaries) => {
          this.summaries = summaries;
          console.log(this.summaries);
        }
      );
      this.message = new Message();
    }
}
