package moviereservation.domain;

import moviereservation.domain.PaymentApproved;
import moviereservation.domain.PaymentCancelled;
import moviereservation.PaymentApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;


@Entity
@Table(name="Payment_table")
@Data

public class Payment  {


    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String approveDate;
    
    
    
    
    
    private Integer amount;
    
    
    
    
    
    private String status;
    
    
    
    
    
    private String qty;
    
    
    
    
    
    private String reservId;
    
    
    
    
    
    private String payId;

    @PostPersist
    public void onPostPersist(){


        PaymentApproved paymentApproved = new PaymentApproved(this);
        paymentApproved.publishAfterCommit();



        PaymentCancelled paymentCancelled = new PaymentCancelled(this);
        paymentCancelled.publishAfterCommit();

    }


    // cc test
    @PostLoad
    public void makeDelay(){
        try {
            System.out.println("Payment PostLoad Delay...");
            Thread.currentThread().sleep((long) (400 + Math.random() * 210));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
     }

    public static PaymentRepository repository(){
        PaymentRepository paymentRepository = PaymentApplication.applicationContext.getBean(PaymentRepository.class);
        return paymentRepository;
    }

    public static void cancelPayment(ReservationCancelled reservationCancelled){

        System.out.println("\n\n##### function cancelPayment id: " + reservationCancelled.getId() + "\n\n");        

        //Saga-2. ReservationCanclled event에서 넘어온 paymentId(Payment의 PK)로 Payment 레코드 검색
        repository().findById(reservationCancelled.getId()).ifPresent(payment->{
            //Saga-3. Saga-3에서 검색된 Payment 레코드의 상태를 Cancelled로 변경
            payment.setStatus("Cancelled");
            repository().save(payment);
         });        
    }

}
