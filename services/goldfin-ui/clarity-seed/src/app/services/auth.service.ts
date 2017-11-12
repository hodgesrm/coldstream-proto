/*
 * Copyright (c) 2017 Goldfin.io. All Rights Reserved.
 */
import { Injectable } from '@angular/core';

import { User } from './user';

class Session {
  userName: string;
  sessionKey: string;
}

const MOCK_USERS: User[] = [
  { name: 'rhodges', password: 'secret' }
];

@Injectable()
export class AuthService {
  authorize(name: string, password: string): Session {
    let user = MOCK_USERS.find(user => user.name === name);
    if (user == null) {
      return null;
    } else if (user.password === password) {
      let session: Session = { userName: name, sessionKey: "1" }
      return session
    } else {
      return null;
    }
  }

  getUser(name: string): User {
    return MOCK_USERS.find(user => user.name === name);
  } 
}
