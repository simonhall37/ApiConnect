import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import {MatButtonModule } from '@angular/material/button';
import { RouterModule, Routes } from '@angular/router';
import { HttpClientModule } from "@angular/common/http";
import {FormsModule} from '@angular/forms';

import { AppComponent } from './app.component';
import { ProfileComponent } from './profile/profile.component';
import { CacheComponent } from './cache/cache.component';

const appRoutes: Routes = [
  { path: 'profiles', component: ProfileComponent }
  , { path: 'cache',      component: CacheComponent }
  // , { path: '**', component: PageNotFoundComponent }
];


@NgModule({
  declarations: [
    AppComponent,
    ProfileComponent,
    ProfileComponent,
    CacheComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatCardModule,
    MatTableModule,
    MatIconModule,
    HttpClientModule,
    FormsModule,
    MatButtonModule,
    RouterModule.forRoot(
      appRoutes
      // , { enableTracing: true }
    )
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
