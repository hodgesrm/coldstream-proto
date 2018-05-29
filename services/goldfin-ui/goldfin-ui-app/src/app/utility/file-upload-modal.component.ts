import {Component, OnInit, Input, Output, EventEmitter } from "@angular/core";
import { DataSeriesService }   from '../services/data-series.service';

export class UploadRequest {
  public files: any = null;
  public description: string = null;
  constructor() {} 
}

@Component({
  selector: "file-upload-modal", 
  templateUrl: "./file-upload-modal.component.html"
})
export class FileUploadModalComponent implements OnInit {
  constructor(
  ) {}

  ngOnInit() {
    console.log("File Upload ngOnInit() invoked");
  }

  // Two way flag to show and clear away modal form. 
  @Input() show: boolean = false;
  @Output() showChange = new EventEmitter();

  // File identifiers and descriptions.  It seems that Javascript 
  // at least on Chrome represents files as a special object that 
  // is corrupted if we try to set a type on it. Consequently we
  // store the file value as an unchecked type.  (NgModel does not 
  // work on the file input type as far as I can tell.)
  files = null;
  description = '';

  resetValues(){
    this.files = null;
    this.description = '';
  }

  // Error message.  If set we'll show an error. 
  errorMessage = null;

  // Emitter to post into upload method supplied by user. 
  @Output() uploadRequest = new EventEmitter<UploadRequest>();

  //onSubmit(request: UploadRequest) {
  onSubmit() {
    console.log("upload() invoked: files=" + this.files
                + ", description=" + this.description);
    if (this.files == null) {
      this.errorMessage = "No file(s) selected for upload";
    } else {
      var upRequest2 = new UploadRequest();
      upRequest2.files = this.files;
      upRequest2.description = this.description;
      this.uploadRequest.emit(upRequest2);
      this.resetValues();
      this.showChange.emit(false);
    }
  }

  onFileChange(event) {
    this.errorMessage = null;
    this.files = event.srcElement.files;
    console.log(event.srcElement.files);
  }

  onCancel() {
    console.log("onCancel() invoked");
    this.resetValues();
    this.showChange.emit(false);
  }
}
