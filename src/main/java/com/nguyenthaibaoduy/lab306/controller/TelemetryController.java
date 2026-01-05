// package com.nguyenthaibaoduy.lab306.controller;

// import com.nguyenthaibaoduy.lab306.model.Telemetry;
// import com.nguyenthaibaoduy.lab306.repository.TelemetryRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import java.util.List;
// @RestController
// @RequestMapping("/telemetry")
// @CrossOrigin(origins = "http://localhost:3000")
// public class TelemetryController {
//     @Autowired
//     private TelemetryRepository telemetryRepository;

//     @GetMapping("/{deviceId}")
//     public List<Telemetry> getByDevice(@PathVariable Long deviceId) {
//         return telemetryRepository.findByDeviceId(deviceId);
//     }
//     @DeleteMapping("/{id}")
// public String deleteTelemetry(@PathVariable Long id) {
//     if (telemetryRepository.existsById(id)) {
//         telemetryRepository.deleteById(id);
//         return "Telemetry deleted";
//     }
//     return "Telemetry not found";
// }

// }