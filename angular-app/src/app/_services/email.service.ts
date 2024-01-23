import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const EMAIL_API = 'http://localhost:8080/api/email'
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class EmailService {


  constructor(private http: HttpClient) { }

  sentEmail(email: string, subjectOfMail: string, textOfEmail: string, tagName: string, tagId: number): Observable<any> {
    return this.http.post(EMAIL_API + '/sent-email', 
    {
      email,
      subjectOfMail,
      textOfEmail, 
      tagName,
      tagId
    }, httpOptions)
  }
}
