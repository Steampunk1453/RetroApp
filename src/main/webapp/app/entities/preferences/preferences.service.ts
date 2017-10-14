import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Rx';

import {Preferences} from './preferences.model';
import {createRequestOption, ResponseWrapper} from '../../shared';

@Injectable()
export class PreferencesService {

    private resourceUrl = 'api/preferences';

    constructor(private http: Http) { }

    create(preferences: Preferences): Observable<Preferences> {
        const copy = this.convert(preferences);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(preferences: Preferences): Observable<Preferences> {
        const copy = this.convert(preferences);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Preferences> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convert(preferences: Preferences): Preferences {
        const copy: Preferences = Object.assign({}, preferences);
        return copy;
    }
}
