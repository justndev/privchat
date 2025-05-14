package com.ndev.privchat.privchat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndev.privchat.privchat.utilities.ParameterStringBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {
    private String btcMangerUrl = "http://localhost:6000";
    private String createPaymentEndpoint = "/payment/create";
    private String checkPaymentEndpoint = "/payment/check";

    public Object createPayment() {
        try {
            URL url = new URL(btcMangerUrl + createPaymentEndpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoOutput(true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            ObjectMapper mapper = new ObjectMapper();

            Dto result = mapper.readValue(content.toString(), Dto.class);
            System.out.println("ID: " + result.getId());
            System.out.println("Address: " + result.getAddress());
            return result;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean checkPaymentById(String id) {
        try {
            URL url = new URL(btcMangerUrl + checkPaymentEndpoint + "?id=" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoOutput(true);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(content.toString(), Boolean.class);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private static class Dto {
        private String id;
        private String address;
    }
}
