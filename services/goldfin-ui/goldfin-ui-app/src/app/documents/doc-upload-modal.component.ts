import {Component, OnInit, Input, Output, EventEmitter } from "@angular/core";

export class UploadRequest {
    constructor(public file: File, public description: string) {}
}

@Component({
    selector: "doc-upload-modal", 
    templateUrl: "./doc-upload-modal.component.html"
})
export class DocUploadModalComponent implements OnInit {
    // Two way communication to show and clear away modal form. 
    @Input() show: boolean = false;
    @Output() showChange = new EventEmitter();

    model = new UploadRequest(null, '');
    files = null;
    errorMessage = null;

    ngOnInit() {
    }

    onFileChange(event) {
      this.errorMessage = null;
      this.files = event.srcElement.files;
      console.log(this.files);
    }

    onSubmit(form: UploadRequest) {
        console.log("onSubmit() invoked: files=" + this.files + ", description="
            + form.description);
        if (this.files == null) {
            this.errorMessage = "No file(s) selected for upload";
        } else {
            form.description = null;
            this.files = null;
            this.showChange.emit(false);
        }
    }

    onCancel() {
        console.log("onCancel() invoked");
        this.files = null;
        this.showChange.emit(false);
    }
}
