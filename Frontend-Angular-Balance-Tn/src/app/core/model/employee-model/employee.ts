export class Employee{

    public employeeIdDataBase  : number;
    public employeeId          : string;
    public username            : string;
    public password            : string;
    public firstName           : string;
    public lastName            : string;
    public function            : string;
    public phoneNumber         : number;
    public nicNumber           : number;
    public profileImageUrl     : string;
    public lastLoginDate       : Date;
    public lastLoginDateDisplay: Date;
    public joindDate           : Date;
    public isEnabled           : boolean;
    public isNotlocked         : boolean;
    public role                : string; 
    public authorities         : [];

    constructor(){
        this.firstName ='';
        this.lastName ='';
        this.username ='';
        this.function='';
        this.phoneNumber = 0;
        this.nicNumber = 0;
        this.isNotlocked = false;
        this.isEnabled = false;
        this.role ='';
        this.authorities =[];
    }


}