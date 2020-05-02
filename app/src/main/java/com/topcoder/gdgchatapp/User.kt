package com.topcoder.gdgchatapp

class User (){
    var uid = ""
    var firstName = ""
    var lastName = ""
    var email = ""
    var token = ""
    constructor( _uid: String,_firstName: String, _lastName: String,  _email: String, _token: String): this(){
        this.uid = _uid
        this.firstName = _firstName
        this.lastName = _lastName
        this.email = _email
        this.token = _token
    }
}