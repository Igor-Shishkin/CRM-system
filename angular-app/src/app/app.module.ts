import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { httpInterceptorProviders } from './_helpers/http.interceptor';
import { NavigationComponent } from './navigation/navigation.component';
import { SideBarComponent } from './side-bar/side-bar.component';
import { ClientsComponent } from './board-user/clients/clients.component';
import { AddLeadComponent } from './board-user/add-lead/add-lead.component';
import { LeadsComponent } from './board-user/leads/leads.component';
import { ClientWorkplaceComponent } from './board-user/client-workplace/client-workplace.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { SaveMessageDialogComponent } from './side-bar/save-message-dialog/save-message-dialog.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { NotPermissionComponent } from './home/not-permission/not-permission.component';
import { DatePipe } from '@angular/common';
import { OrderWorkplaceComponent } from './board-user/order-workplace/order-workplace.component';
import { ItemCalculationComponent } from './board-user/order-workplace/item-calculation/item-calculation.component';
import { ConfirmSigningContractComponent } from './board-user/order-workplace/confirm-signing-contract/confirm-signing-contract.component';
import { ConfirmPainmentComponent } from './board-user/order-workplace/confirm-painment/confirm-painment.component';
import { MessageMenuComponent } from './side-bar/message-menu/message-menu.component';
import { ConfirmDeleteMessageComponent } from './side-bar/message-menu/confirm-delete-messsage/confirm-delete-message.component';
import { CreateNewOrderComponent } from './board-user/client-workplace/create-new-order/create-new-order.component';
import { RegisterNewUserComponent } from './board-admin/register-new-user/register-new-user.component';
import { ConfirmDeleteUserComponent } from './board-admin/confirm-delete-user/confirm-delete-user.component';
import { UsersComponent } from './board-admin/users/users.component';
import { SentEmailComponent } from './sent-email/sent-email.component';
import { RouterModule } from '@angular/router';
import { BlackListOfClientsComponent } from './board-user/black-list-of-clients/black-list-of-clients.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    ProfileComponent,
    BoardAdminComponent,
    BoardUserComponent,
    NavigationComponent,
    LeadsComponent,
    SideBarComponent,
    ClientsComponent,
    AddLeadComponent,
    ClientWorkplaceComponent,
    SaveMessageDialogComponent,
    NotPermissionComponent,
    OrderWorkplaceComponent,
    ItemCalculationComponent,
    ConfirmSigningContractComponent,
    ConfirmPainmentComponent,
    MessageMenuComponent,
    ConfirmDeleteMessageComponent,
    CreateNewOrderComponent,
    RegisterNewUserComponent,
    ConfirmDeleteUserComponent,
    SentEmailComponent,
    UsersComponent,
    BlackListOfClientsComponent
  ],
  imports: [
    BrowserModule,
    RouterModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatDialogModule,
    MatButtonModule,
    MatDatepickerModule,
    MatInputModule,
    ReactiveFormsModule,
    MatNativeDateModule,
    ReactiveFormsModule
  ],
  providers: [httpInterceptorProviders, DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }