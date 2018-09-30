/*
 * Copyright (c) 2016 VMware, Inc. All Rights Reserved.
 */
import { async, TestBed, ComponentFixture } from "@angular/core/testing";
import { ClarityModule } from '@clr/angular';
import { LoginComponent } from './login.component';


describe('LoginComponent', () => {

    let expectedMsg: string = 'Login or register a new account';

    let fixture: ComponentFixture<any>;
    let compiled: any;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [
                LoginComponent
            ],
            imports: [
                //ClarityModule.forRoot()
            ]
        });

        fixture = TestBed.createComponent(LoginComponent);
        fixture.detectChanges();
        compiled = fixture.nativeElement;

    });

    afterEach(() => {
        fixture.destroy();
    });

    it('should create the login page', async(() => {
        expect(compiled).toBeTruthy();
    }));

    it(`should display: "${expectedMsg}"`, async(() => {
        expect(compiled.querySelector("p").textContent).toMatch(expectedMsg);
    }));


});
