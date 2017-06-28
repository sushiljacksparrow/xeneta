package hello;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dao.DateParser;
import dao.PortDAO;
import dao.PriceDAO;
import dao.RegionDAO;
import models.Price;
import models.PriceResponse;

@RestController
public class RateController {

    private static final Logger log = LoggerFactory.getLogger(RateController.class);

    @Autowired private PortDAO portDAO;
    @Autowired private RegionDAO regionDAO;
    @Autowired private PriceDAO priceDAO;

    // rates?date_from=2016-01-01&date_to=2016-01-10&origin=CNSGH&destination=NLRTM
    @RequestMapping(value = "/rates", method = RequestMethod.GET, produces = "application/json")
    public List<PriceResponse> getDailyAverageRate(@RequestParam("date_from") final String dateFrom,
            @RequestParam("date_to") final String dateTo, @RequestParam("origin") final String origin,
            @RequestParam("destination") final String destination) {

        Date startDate = DateParser.toDate(dateFrom);
        Date endDate = DateParser.toDate(dateTo);

        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        List<String> originPorts = checkPortOrReturnAllPortsInRegion(origin);
        List<String> destinationPorts = checkPortOrReturnAllPortsInRegion(destination);
        
        log.info(
                String.format("Getting all the rates from origins %s, destinations %s", originPorts, destinationPorts));

        return getAveragePrice(originPorts, destinationPorts, startLocalDate, endLocalDate);

    }

    private List<String> checkPortOrReturnAllPortsInRegion(String origin) {
        List<String> ports = new ArrayList<>();
        if (portDAO.isPort(origin)) {
            ports.add(origin);
        } else if (regionDAO.isSlug(origin)) {
            ports.addAll(portDAO.getAllPortsForSlug(origin));
        }
        return ports;
    }

    private List<PriceResponse> getAveragePrice(List<String> origins, List<String> destinations, LocalDate startDate,
            LocalDate endLocalDate) {
        List<PriceResponse> priceResponses = new ArrayList<>();
        for (LocalDate date = startDate; date.equals(endLocalDate)
                || date.isBefore(endLocalDate); date = date.plusDays(1)) {
            String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            List<Price> prices = new ArrayList<>();
            for (String origin : origins) {
                for (String destination : destinations) {
                    prices.addAll(priceDAO.getPrices(origin, destination, dateString));
                }
            }

            int sum = 0;
            for (Price price : prices) {
                sum += price.getPrice();
            }
            PriceResponse priceResponse = new PriceResponse(dateString, prices.size() < 3 ? 0 : sum / prices.size());
            priceResponses.add(priceResponse);
        }
        return priceResponses;
    }

    @RequestMapping(value = "/rates", method = RequestMethod.POST)
    public void setDailyAverageRate(@RequestParam("date_from") final String dateFrom,
            @RequestParam("date_to") final String dateTo, @RequestParam("origin") final String orginPort,
            @RequestParam("destination") final String destination, @RequestParam("price") final int price) {
        Date startDate = DateParser.toDate(dateFrom);
        Date endDate = DateParser.toDate(dateTo);

        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        for (LocalDate date = startLocalDate; date.equals(endLocalDate)
                || date.isBefore(endLocalDate); date = date.plusDays(1)) {
            String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            log.info(String.format("Setting up price for origin %s destination %s date %s price %d",
                    orginPort,
                    destination,
                    dateString,
                    price));
            priceDAO.setPrice(orginPort, destination, dateString, price);
        }
    }

}
