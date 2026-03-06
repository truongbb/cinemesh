package com.cinemesh.notificationservice.email;

import com.cinemesh.common.dto.domain.OrderDomainDto;
import com.cinemesh.common.dto.domain.TicketDomainDto;
import com.cinemesh.common.dto.domain.UserDomainDto;
import com.cinemesh.notificationservice.infrastructure.feign.TheaterFeignClient;
import com.cinemesh.notificationservice.infrastructure.feign.UserFeignClient;
import com.cinemesh.notificationservice.infrastructure.feign.response.SeatResponse;
import com.cinemesh.notificationservice.infrastructure.feign.response.ShowtimeResponse;
import com.cinemesh.notificationservice.infrastructure.feign.response.UserResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailService {

    @Value("${spring.mail.username}")
    String fromEmail;

    @Value("${application.account.activation.activationUrl}")
    String activationLink;

    @Value("${application.account.passwordForgotten.url}")
    String passwordForgottenLink;

    final JavaMailSender javaMailSender;
    final UserFeignClient userFeignClient;
    final TheaterFeignClient theaterFeignClient;

    public void sendActivationMail(UserDomainDto user) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(user.getEmail());

        String url = activationLink.replace("{id}", user.getId().toString());
        String content = "<div>" +
                "<h1>Welcome to Cinemesh</h1>" +
                "<div>Please click <a href='" + url + "'>this link</a> to activate your account</div>" +
                "</div>";

        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setSubject("Registration Confirmation");

        javaMailSender.send(mimeMessage);
    }

    public void sendForgotPasswordMail(UserDomainDto user) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(user.getEmail());

        String url = passwordForgottenLink.replace("{id}", user.getId().toString());
        String content = "<div>" +
                "<h1>Forgot password</h1>" +
                "<div>Please click <a href='" + url + "'>this link</a> to change your password</div>" +
                "</div>";

        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setSubject("Password Forgotten");

        javaMailSender.send(mimeMessage);
    }

    public void sendOrderCompletedEmail(OrderDomainDto orderDomainDto) throws MessagingException {
        UserResponse user = userFeignClient.getUserById(orderDomainDto.getUserId());
        ShowtimeResponse showtime = theaterFeignClient.getShowtimeDetail(orderDomainDto.getTickets().get(0).getShowtimeId());

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(user.getEmail());

        List<UUID> seatIds = orderDomainDto.getTickets().stream().map(TicketDomainDto::getSeatId).toList();
        List<SeatResponse> seats = showtime.getRoom().getSeats()
                .stream()
                .filter(seat -> seatIds.contains(seat.getId()))
                .toList();
        String seatCodes = seats
                .stream()
                .map(seat -> seat.getRowCode() + seat.getColumnNumber())
                .collect(Collectors.joining(", "));

        String content = "<div>" +
                "<h1>Congratulations</h1>" +
                "<div>Your ticket(s) have been ordered successfully! Here's details:</div>" +
                "<br>" +
                "<div>Movie: " + showtime.getMovie().getVnTitle() + "(" + showtime.getMovie().getEngTitle() + ")" + "</div>" +
                "<div>Showtime: " + showtime.getStartTime() + " to " + showtime.getEndTime() + "</div>" +
                "<div>Room: " + showtime.getRoom().getName() + "</div>" +
                "<div>Seats: " + seatCodes + "</div>" +
                "<div>Total amount: " + orderDomainDto.getTotalAmount() + "VND</div>" +
                "<div>Payment status: " + orderDomainDto.getPaymentStatus() + "</div>" +
                "<br>" +
                "<div>Thank you for choosing Cinemesh! See you later at our theater and hope you enjoy the movie!</div>";

        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setSubject("[Cinemesh] Order successfully");

        javaMailSender.send(mimeMessage);
    }
}
