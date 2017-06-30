package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dao.DateParser;
import dao.ExchangeRateDAO;
import dao.PortDAO;
import dao.PriceDAO;
import dao.RegionDAO;
import models.Price;
import models.PriceRequest;
import models.PriceResponse;
import models.Region;

@RestController
public class RateController {

    private static final Logger log = LoggerFactory.getLogger(RateController.class);

    private static final String ROOT_REGION = "ROOT";

    @Autowired private PortDAO portDAO;
    @Autowired private RegionDAO regionDAO;
    @Autowired private PriceDAO priceDAO;
    @Autowired private ExchangeRateDAO exchangeRateDAO;

    // rates?date_from=2016-01-01&date_to=2016-01-10&origin=CNSGH&destination=NLRTM

    /**
     * 
     * @param dateFrom
     *            - format 2017-12-30
     * @param dateTo
     *            - format 2017-12-20
     * @param origin
     *            - ABCD
     * @param destination
     *            - DEGG
     * @return List of average prices for each date. If there is no average price for a date then average price value is
     */
    @ResponseStatus(code=HttpStatus.OK)
    @RequestMapping(value = "/rates", method = RequestMethod.GET, produces = "application/json")
    public List<PriceResponse> getDailyAverageRate(@RequestParam("date_from") final String dateFrom,
            @RequestParam("date_to") final String dateTo, @RequestParam("origin") final String origin,
            @RequestParam("destination") final String destination) {

        Date startDate = DateParser.toDate(dateFrom);
        Date endDate = DateParser.toDate(dateTo);

        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        if (endLocalDate.isBefore(startLocalDate)) {
            throw new IllegalArgumentException(
                    String.format("date_from %s can not be later than date_end", dateFrom, dateTo));
        }

        List<String> originPorts = checkPortOrReturnAllPortsInRegion(origin);
        List<String> destinationPorts = checkPortOrReturnAllPortsInRegion(destination);

        log.info(
                String.format("Getting all the rates from origins %s, destinations %s", originPorts, destinationPorts));

        List<PriceResponse> averagePrices = getAveragePrice(originPorts,
                destinationPorts,
                startLocalDate,
                endLocalDate);

        // check if all the prices are empty
        boolean allPricesAreEmpty = true;
        for (PriceResponse priceResponse : averagePrices) {
            if (priceResponse.getAveragePrice() != 0) {
                allPricesAreEmpty = false;
                break;
            }
        }

        if (!allPricesAreEmpty) {
            return averagePrices;
        } else {
            boolean reachedRoot = false;
            while (allPricesAreEmpty && !reachedRoot) {
                String parentOrigin = parent(origin);
                String parentDestination = parent(destination);

                List<String> originDescendentPorts = getDescendentPorts(parentOrigin).stream()
                        .map(Region::getSlug)
                        .collect(Collectors.toList());
                List<String> destinationDescendentPorts = getDescendentPorts(parentDestination).stream()
                        .map(Region::getSlug)
                        .collect(Collectors.toList());
                averagePrices = getAveragePrice(originDescendentPorts,
                        destinationDescendentPorts,
                        startLocalDate,
                        endLocalDate);
                if (!averagePrices.isEmpty()) {
                    allPricesAreEmpty = false;
                }

                if (ROOT_REGION.equals(parentOrigin) && ROOT_REGION.equals(parentDestination)) {
                    reachedRoot = true;
                }

            }
        }
        return averagePrices;
    }

    private List<Region> getDescendentPorts(String parent) {
        List<Region> childrenSlug = regionDAO.getChildrenForSlug(parent);
        for (Region region : childrenSlug) {
            childrenSlug.addAll(getDescendentPorts(region.getSlug()));
        }
        return childrenSlug;
    }

    private String parent(String origin) {
        if (portDAO.isPort(origin)) {
            return portDAO.getSlugForPort(origin);
        } else {
            return regionDAO.getParentForSlug(origin);
        }
    }

    private List<String> checkPortOrReturnAllPortsInRegion(String port) {
        List<String> ports = new ArrayList<>();
        if (portDAO.isPort(port)) {
            ports.add(port);
        } else if (regionDAO.isSlug(port)) {
            ports.addAll(portDAO.getAllPortsForSlug(port));
        } else {
           throw new IllegalArgumentException(String.format("%s is invalid", port)); 
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
    public void setDailyAverageRate(@RequestBody PriceRequest priceRequest) {
        log.info(String.format("Input for post request %s", priceRequest.getFromDate()));
        Date startDate = DateParser.toDate(priceRequest.getFromDate());
        Date endDate = DateParser.toDate(priceRequest.getToDate());

        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int valueInUSD = "USD".equals(priceRequest.getCurrency()) ? priceRequest.getValue()
                : exchangeRateDAO.convertToUSD(priceRequest.getValue(), priceRequest.getCurrency());

        for (LocalDate date = startLocalDate; date.equals(endLocalDate)
                || date.isBefore(endLocalDate); date = date.plusDays(1)) {
            String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            log.info(String.format("Setting up price for origin %s destination %s date %s price %d",
                    priceRequest.getOrigin(),
                    priceRequest.getDestination(),
                    dateString,
                    valueInUSD));
            priceDAO.setPrice(priceRequest.getOrigin(), priceRequest.getDestination(), dateString, valueInUSD);
        }
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<?> upload(InputStream is) {
        String fileId = UUID.randomUUID().toString();

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream("/tmp/" + fileId);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return ResponseEntity.ok(fileId);
        } catch (IOException e) {
            log.error("Error writing file to disk");
            return ResponseEntity.status(500).body("Server failure");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }

    }

}
