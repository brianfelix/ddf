/**
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 *
 **/
import * as React from 'react'
import UserComponent from './presentation'
const user = require('../../component/singletons/user-instance.js')
const properties = require('../../js/properties.js')
import fetch from '../utils/fetch'

interface State {
  username: string
  isGuest: boolean
  email: string
}

class UserContainer extends React.Component<{}, State> {
  constructor(props: {}) {
    super(props)
    this.state = {
      username: user.isGuest() ? 'Guest' : user.getUserName(),
      isGuest: user.isGuest(),
      email: user.getEmail(),
    }
  }
  signOut = async () => {
    if (properties.logoutUrl && properties.logoutUrl != '') {
      const res = await fetch(
        'https://localhost:8993/logout/?service=' + window.location.href,
        {
          method: 'GET',
          credentials: 'same-origin',
          headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json',
            'X-Requested-With': 'XMLHttpRequest',
          },
        }
      )
      if (!res.ok) {
        console.log('!res.ok')
        console.log(res)
        return
      } else {
        console.log('res is ok!')
        console.log(res)
      }
      window.location.href = properties.logoutUrl
      //     } else {
      //     window.location.href = '../../logout/?service=' + window.location.href
    }
  }
  render() {
    const { username, isGuest, email } = this.state
    return (
      <UserComponent
        username={username}
        isGuest={isGuest}
        email={email}
        signOut={this.signOut.bind(this)}
      />
    )
  }
}

export default UserContainer
