package com.ls.merkbot;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesService {
    private RolesRepository repRoles;

    public RolesService(RolesRepository repRoles){
        this.repRoles = repRoles;
    }

    public List<Roles> listarRol(){
        return repRoles.findAll();
    }
}
