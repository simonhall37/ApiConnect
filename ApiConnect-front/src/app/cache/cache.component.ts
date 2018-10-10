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
          summaries.forEach(
            (s) => {
              this.summaries.push(s);
            }
          );
        }
      );

      this.message = new Message();
    }

    // general functions
    hideMessage(){
      this.message.show = false;
    }
    isError()  {
      if (this.message.type === "error") return true;
      else return false;
    }
    handleError(err: HttpErrorResponse){
      this.message.type = "error";
      if (err.status === 404) this.message.content = "Error connecting to server";
      else this.message.content = err.message;
      this.message.show = true;
    }
    editParam(param:Pair){
      param.editMode = !param.editMode;
      var d = new Date(+param.value*1000);
      console.log(d);
    }
    editFilter(filter: Filter){
      filter.editMode = !filter.editMode;
    }
    editLookup(lookup:Pair){
      lookup.editMode = !lookup.editMode;
    }
    updateCacheSummary(event, summary:CacheSummary) {
      if (event.keyCode === 13){
        summary.editMode = !summary.editMode;
        this.update(summary);
      }
    }
    updateCacheSummarySubObject(event, summary:CacheSummary, pair: Pair) {
      if (event.keyCode === 13){
        if (pair.key!="" && pair.value!=""){
          pair.editMode = !pair.editMode;
          this.update(summary);
        }
        else {
          this.message.type="error";
          this.message.show = true;
          this.message.content = "Empty value not allowed!";
        }
      }
    }
    updateCacheSummaryFilter(event, summary:CacheSummary, filter: Filter) {
      if (event.keyCode === 13){
        if (filter.type != null && filter.type !=""){
          if (filter instanceof TextFilter){
            var tf: TextFilter = filter as TextFilter;
            if ((tf.targetField!=null && tf.targetField != "")  && (tf.validString!=null && tf.validString != "")){
              filter.editMode = !filter.editMode;
              this.update(summary);
            }
            else {
              this.message.type="error";
              this.message.show = true;
              this.message.content = "Empty value not allowed!";
            }
          }
        }
        else {
          this.message.type="error";
          this.message.show = true;
          this.message.content = "Empty value not allowed!";
        }
      }
    }
    removeParam(summary: CacheSummary,param: Pair){
      var index = summary.params.indexOf(param);
      if (index > -1) {
        summary.params.splice(index, 1);
      }
      this.update(summary);
    }
    removeLookup(summary: CacheSummary,lookup: Pair){
      var index = summary.lookupSummaries.indexOf(lookup);
      if (index > -1) {
        summary.lookupSummaries.splice(index, 1);
      }
      this.update(summary);
    }
    removeFilter(summary: CacheSummary,filter: Filter){
      summary.filter = null;
      this.update(summary);
    }
    addParam(summary: CacheSummary){
      summary.params.push(new Pair("",""));
    }
    addLookup(summary: CacheSummary){
      summary.lookupSummaries.push(new Pair("",""));
    }
    addFilter(summary: CacheSummary){
      if (summary.filter === null){
        summary.filter = new TextFilter();
      }
    }

    // api operations
    editSummary(summary: CacheSummary){
      let index = this.summaries.indexOf(summary);
      if (summary.editMode === true){
        summary.editMode = !summary.editMode;
        this.update(summary);
      }
      else {
        // turn off edit mode for all other profiles (without submitting)
        for (var s of this.summaries){
          s.editMode=false;
        }
        summary.editMode = !summary.editMode;
      }
    }
    update(summary: CacheSummary){
      this.cacheApiService.putCacheSummary(summary).subscribe(
        (response) => {
          this.message.type = "info";
          this.message.content = "Cache Summary updated successfully";
          this.message.show = true;
        },
        (err: HttpErrorResponse) => {
          this.handleError(err);
        }
      );
    }
    executeCache(summary: CacheSummary){
      if (!summary.inProgress){
        summary.inProgress=true;
        this.cacheApiService.cache(summary).subscribe(
          (response: CacheSummary) => {
            this.message.type = "info";
            this.message.content = "Updated on " + response.updatedOn + ". " + response.size + " entities cached";
            this.message.show = true;
            summary.inProgress=false;
            summary.updatedOn = response.updatedOn;
          },
          (err: HttpErrorResponse) => {
            this.handleError(err);
            summary.inProgress = false;
          }
        )
      }
      else {
        this.message.type = "error";
        this.message.content = "Already performing cache, wait until it is finished!";
        this.message.show = true;
      }
    }

}
