package usthb.fi.fichevoeux.user;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {
          private final UserService userService ;

    public UserController(UserService userService) {
        this.userService = userService ;
    }

    @GetMapping("/user")
    public List<User> getUsers() {
        return userService.getUsers() ;
    }


    @PostMapping("/user")
    public String registerNewUser(@RequestBody User user) {
        userService.addUser(user);
        return "User : created successfully ! \n" ;
    }

       @DeleteMapping(path = "/user/{userId}")
    public String deleteUser(@PathVariable("userId")  long id) {
        userService.deleteUser(id) ;
        return " deleted successfully !\n" ;
    }

    @PutMapping("user/{id}")
    public String updateUser(@PathVariable long id, @RequestBody User newUser) {
        userService.updateUser(id , newUser) ;

        return "update done";
    }


}
