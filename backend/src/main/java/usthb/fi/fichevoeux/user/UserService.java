package usthb.fi.fichevoeux.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import usthb.fi.fichevoeux.Exception.ResourceNotFoundException;



@Service
public class UserService {


    private final UserRepository userRepository ;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository ;
    }


    public List<User> getUsers() {
        return userRepository.findAll() ;
    }

    public void addUser(User user) {
        
        try {
            
            userRepository.saveAndFlush(user) ;
        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("done ");
    }


    public void deleteUser(long id) {
        
        
        Optional<User> user = userRepository.findById(id) ;
        
        if (user.isPresent()) {
            
            userRepository.deleteById(id);
        } else {
            System.out.println("User with id : "+id+"doesnt exist");
        }
    }


    public void updateUser(long id, User newUser) {
        User oldUser = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("user not found with id " + id)) ;

        oldUser.setEmail(newUser.getEmail());
        oldUser.setName(newUser.getName());
        oldUser.setPassword(newUser.getPassword());
        oldUser.setRole(newUser.getRole());


        userRepository.save(oldUser) ;
    }
    
}
