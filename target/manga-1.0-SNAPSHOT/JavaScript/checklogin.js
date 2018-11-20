/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


window.addEventListener('load', function(){
    
    document.getElementById("buttonlogin").addEventListener('click', function(){
        
    var name = document.getElementById("textname").value;
    var password = document.getElementById("textpassword").value;
    
    
    var flag = false;
    
    if(name.length > 0 && password.length > 0){
        
        flag = true;
    }
    if(flag){
        document.getElementById('formlogin').submit();
    }else{
        alert('Por favor, no deje ningún campo vacío.');
    }
    });
    
});
