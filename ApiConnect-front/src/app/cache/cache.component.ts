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
  newSummary: CacheSummary;

  constructor(private cacheApiService: CacheApiService) {}

    ngOnInit() {
      this.newSummary = new CacheSummary();
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
    // edit items
    editParam(summary: CacheSummary,param:Pair){
      if (param.editMode){
        this.updateSubObject(summary,param);
      }
      else param.editMode = !param.editMode;
    }
    editFilter(summary:CacheSummary,filter: Filter){
      if (filter.editMode){
        this.updateFilter(summary,filter);
      }
      else filter.editMode = !filter.editMode;

    }
    editLookup(summary:CacheSummary,lookup:Pair){
      if (lookup.editMode){
        this.updateSubObject(summary,lookup);
      }
      else lookup.editMode = !lookup.editMode;
    }
    // update summary or sub objects
    updateCacheSummary(event, summary:CacheSummary) {
      if (event.keyCode === 13){
        summary.editMode = !summary.editMode;
        this.update(summary);
      }
    }
    updateCacheSummarySubObject(event, summary:CacheSummary, pair: Pair) {
      if (event.keyCode === 13){
        this.updateSubObject(summary,pair);
      }
    }
    updateSubObject(summary:CacheSummary, pair:Pair){
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
    updateFilter(summary: CacheSummary, filter : Filter){
      if (filter.type != null && filter.type !=""){
        // if (filter instanceof TextFilter){
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
        // } else {
        //   console.log(filter);
        // }
      }
      else {
        this.message.type="error";
        this.message.show = true;
        this.message.content = "Empty value not allowed!";
      }
    }
    updateCacheSummaryFilter(event, summary:CacheSummary, filter: Filter) {
      if (event.keyCode === 13){
        this.updateFilter(summary,filter);
      }
    }
    // remove sub objects
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
    // add sub objects
    addParam(summary: CacheSummary){
      summary.params.push(new Pair("",""));
      summary.params.forEach((p) => {
        if (p.key == null || p.key == "" || p.value == null || p.key == null) {
          p.editMode = true;
        }
      });
    }
    addLookup(summary: CacheSummary){
      summary.lookupSummaries.push(new Pair("",""));
      summary.lookupSummaries.forEach((lkp) => {
        if (lkp.key == null || lkp.key == "" || lkp.value == null || lkp.key == null) {
          lkp.editMode = true;
        }
      });
    }
    addFilter(summary: CacheSummary){
      if (!summary.filter){
        summary.filter = new TextFilter();
        summary.filter.editMode = true;
      }
    }

    // api operations
    // PUT
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
            summary.size = response.size;
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

    // DELETE
    removeSummary(element: CacheSummary){
      this.cacheApiService.deleteCacheSummary(element).subscribe(
        (response) => {
          this.summaries.splice(this.summaries.indexOf(element),1);
          this.message.type = "info";
          this.message.content = "Cache Summary deleted successfully";
          this.message.show = true;
        },
        (err: HttpErrorResponse) => {
          this.handleError(err);
        }
      );
    }

  // post
  add(event){
    if (event.keyCode === 13){
      this.createSummary();
    }
  }
  createSummary(){
  this.newSummary.lookupSummaries = [];
  this.newSummary.params = [];
  this.cacheApiService.postCacheSummary(this.newSummary).subscribe(
    (response) => {
      this.message.type = "info";
      this.message.content = "Cache Summary created successfully";
      this.message.show = true;
      this.summaries.push(this.newSummary);
      this.newSummary = new CacheSummary();
    },
    (err: HttpErrorResponse) => {
      this.handleError(err);
    }
  );
  }

}
