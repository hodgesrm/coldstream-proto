/*
 * Copyright (c) 2018 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { DataService as DataApi } from '../client/api/api';
import { DataSeries } from '../client/model/models';

@Injectable()
export class DataSeriesService {
  constructor(
    private inventoryApi: DataApi
  ) {}

  createDataSeries(file: File, description: string): Observable<DataSeries> {
    return this.inventoryApi.dataCreate(file, description);
  }

  loadDataSeries(): Observable<Array<DataSeries>> {
    return this.inventoryApi.dataShowAll();
  }

  getBase64(file: File): Promise<{}> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = error => reject(error);
    });
  }

  uploadDataSeries(files: File[], description): Promise<{}> {
    var promises = [];
    var component = this;
    for (var i = 0; i < files.length; i++) {
      var file = files[i];
      console.log("Upload scheduled: " + file.name);
      var next = component.inventoryApi.dataCreate(
                 file, description, null, true);
      next.subscribe(
        data => {console.log(data);},
        error => {console.log(error);}
      );
      return Promise.all(promises);
    }
  }

  processDataSeries(dataSeries: DataSeries[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < dataSeries.length; i++) {
      var dataS = dataSeries[i];
      console.log("Processing scheduled: " + dataS.contentType);
      console.log("Processing scheduled: " + dataS.id);
      console.log("Processing scheduled: " + dataS);
      var next = this.inventoryApi.dataProcess(dataS.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }

  deleteDataSeries(dataSeries: DataSeries[]): Promise<{}> {
    var promises = [];
    for (var i = 0; i < dataSeries.length; i++) {
      var ds = dataSeries[i];
      console.log("Delete scheduled: " + ds.id);
      var next = this.inventoryApi.dataDelete(ds.id).toPromise();
      promises.push(next);
    }
    return Promise.all(promises)
  }
}
