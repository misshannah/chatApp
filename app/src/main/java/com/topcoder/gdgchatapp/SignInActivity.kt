package com.topcoder.gdgchatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class SignInActivity : AppCompatActivity() {

    private lateinit var googleClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        setupGoogleClient()
        login.setOnClickListener {
            val signInIntent: Intent = googleClient.signInIntent
            startActivityForResult(signInIntent, REQUEST_CODE)
        }
    }

    private fun setupGoogleClient() {
        val googleSignInOpt = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleClient = GoogleSignIn.getClient(this, googleSignInOpt)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
               account?.idToken?.let {firebaseAuthWithGoogle(it, account)  }

            } catch (e: Exception){
                Toast.makeText(this, "Error Logging in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(this, "Error Logging in", Toast.LENGTH_SHORT).show()
                                return@OnCompleteListener
                            }
                            val token = task.result?.token
                        val user = User(_uid = auth.currentUser?.uid?: "",_firstName = account.givenName ?: "", _lastName = account.familyName
                            ?:"", _email = account.email ?:"", _token = token?: "")

                        database.reference.child("Users").child(user.uid).setValue(user)
                        startActivity(Intent(this ,UserListActivity::class.java ).apply {
                            putExtra(Constants.INTENT_KEY_USER_TOKEN,token)
                        })
                        finish()
                        })


                } else {
                    Toast.makeText(this, "Error Logging in", Toast.LENGTH_SHORT).show()
                }

            }
    }
}
