package ua.edu.chdtu.deanoffice.entity;

public enum Payment {
    CONTRACT, BUDGET;

    public static Payment getPaymentFromUkrName(String paymentName){
        String paymentNameInUppercase = paymentName.toUpperCase();
        if (paymentNameInUppercase.equals("КОНТРАКТ")){
            return CONTRACT;
        }
        if (paymentNameInUppercase.equals("БЮДЖЕТ")){
            return BUDGET;
        }
        return null;
    }
}
