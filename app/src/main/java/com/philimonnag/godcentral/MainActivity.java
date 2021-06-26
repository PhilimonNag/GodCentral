package com.philimonnag.godcentral;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.philimonnag.godcentral.databinding.ActivityMainBinding;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
         NavigationUI.setupWithNavController(binding.navView, navController);
         navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
             @Override
             public void onDestinationChanged(@NotNull NavController navController, @NotNull NavDestination navDestination, @Nullable Bundle bundle) {
                 switch (navDestination.getId()){
                     case  R.id.homeFragment:
                     show();
                     break;
                     case R.id.chatFragment:
                         show();
                         break;
                     case R.id.profileFragment:
                         show();
                         break;
                     case R.id.notificationFragment:
                         show();
                         break;
                     default:
                         hide();

                 }
             }
         });
    }

    private void show() {
        binding.navView.setVisibility(View.VISIBLE);
    }
   private void hide(){
       binding.navView.setVisibility(View.GONE);
   }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

//    @Override
//    public void onBackPressed() {
//
//        int count = getFragmentManager().getBackStackEntryCount();
//
//        if (count == 0) {
//            super.onBackPressed();
//            //additional code
//            new AlertDialog.Builder(this)
//                    .setTitle("Do you want to exit God Central?")
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            }).show();
//        } else {
//            getFragmentManager().popBackStack();
//        }
//
//    }
}