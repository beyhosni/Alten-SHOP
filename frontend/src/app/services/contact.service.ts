import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Contact } from '../models/contact.model';

@Injectable({
    providedIn: 'root'
})
export class ContactService {
    private readonly API_URL = 'http://localhost:8080/api/contact';

    constructor(private http: HttpClient) { }

    sendMessage(contact: Contact): Observable<Contact> {
        return this.http.post<Contact>(this.API_URL, contact);
    }
}
