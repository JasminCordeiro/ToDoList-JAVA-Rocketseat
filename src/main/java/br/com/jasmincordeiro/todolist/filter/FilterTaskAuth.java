package br.com.jasmincordeiro.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.jasmincordeiro.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                var servletPath = request.getServletPath();
                if(servletPath.startsWith("/tasks/")){
                //Pegar a autentitação (usuário e senha)
                var authorization =  request.getHeader("Authorization");
                var authEncoded = authorization.substring("Basic".length()).trim(); //retirando a palavra "Basic"

                byte[] authDecoded = Base64.getDecoder().decode(authEncoded); // decodificando a autenticação
                var authString = new String(authDecoded); //criando uma String para armazenar a decodificação

                
                String[] credentials  = authString.split(":"); // retiramos os ":" para ficar visivelmente mais bonito e dividimos dentro de uma Array
                String username = credentials[0]; //inf 1 na posição 0 da Array
                String password = credentials[1]; //inf 2 na posição 1 da Array

                System.out.println("Authorization");
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                System.out.println("___________________________________________");

                //Validar usuário

                var user = this.userRepository.findByUsername(username);

                if (user == null) {
                    response.sendError(401);
                } else {
                    //Validar senha
                    var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                    if(passwordVerify.verified){
                        //Continue a nadar...
                        request.setAttribute("idUser",user.getId());
                        filterChain.doFilter(request, response);
                    }else{
                        response.sendError(401);
                    }
                   
                }
                }else{
                    filterChain.doFilter(request, response);
                }
            }

    
}
