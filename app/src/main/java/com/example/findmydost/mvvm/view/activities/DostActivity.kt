package com.example.findmydost.mvvm.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.findmydost.DostApplication
import com.example.findmydost.R
import com.example.findmydost.databinding.ActivityDostBinding
import com.example.findmydost.mvvm.model.User
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_dost.*
import kotlinx.android.synthetic.main.toolbar.view.*
import javax.inject.Inject

@AndroidEntryPoint
class DostActivity : AppCompatActivity() {


    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    @Inject
    lateinit var mGoogleSingnInClient: GoogleSignInClient;

    @Inject
    lateinit var loginManager: LoginManager;


    lateinit var mEmail: TextView;

    lateinit var mUserName: TextView;


    lateinit var binding: ActivityDostBinding

    @Inject
    lateinit var mUser: User;

    @Inject
    lateinit var mFirebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityDostBinding>(this, R.layout.activity_dost)
//        setSupportActionBar(binding.DostToolbar.toolbar)


        initDrawer()
        val navController = findNavController(R.id.nav_host_fragment)

        nav_view.setupWithNavController(navController)
        //  bottomNavigationView.setupWithNavController(navController)

        var headerView = nav_view.getHeaderView(0)
        mUserName = headerView.findViewById<TextView>(R.id.txt_name)
        mEmail = headerView.findViewById<TextView>(R.id.txt_email)

        mUserName.setText(mUser.displayName);
        mEmail.setText((mUser.email))
        headerView.findViewById<CircleImageView>(R.id.profile_pic).let {
            Glide.with(this).load(mUser.photoUrl).into(it)
        }
        navController
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
//                    R.id.mapFragment, R.id.groupFragment, R.id.settingsFragment ->
//                        bottomNavigationView.visibility = View.VISIBLE
//                    else -> bottomNavigationView.visibility = View.GONE
                }
            }


        nav_view.setNavigationItemSelectedListener { menuItem ->
            val id: Int = menuItem.getItemId()
            if (id == R.id.signout) {
                signOut();

            }

            drawer.closeDrawer(GravityCompat.START)
            true
        }

    }


    fun signOut(): Boolean {

        // Firebase sign out
        mFirebaseAuth.signOut()
        loginManager.logOut();
        // Google sign out
        mGoogleSingnInClient.signOut().let {
            if (it.isSuccessful) {


            }
        }

        var intent = Intent(this@DostActivity, RegisterActivity::class.java);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        startActivity(intent)
        finish()
        Toast.makeText(this, "User Signed Out", Toast.LENGTH_SHORT).show()

        return true
    }


    private fun initDrawer() {
        drawer = findViewById(R.id.drawer_layout)
        binding.DostToolbar.toolbar.setNavigationIcon(R.drawable.ic_launcher_background)
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            binding.DostToolbar.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState();
        supportActionBar?.title = "";


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish();
    }


    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()

}


