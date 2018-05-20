package route.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.graph.Node;
import route.services.fields.PlaceFields;
import route.storage.entities.Place;
import route.storage.entities.WalkMeCategory;

import java.util.ArrayList;
import java.util.List;

public class PlaceHolder {
    private static final List<Node> nodeList = new ArrayList<>();
    private static final EntityService<Place, String, PlaceFields> service = new PlaceService();
    private static volatile boolean isLoaded = false;

    private static final Logger logger = LogManager.getLogger(PlaceHolder.class);

    public static void load() {
        if (!isLoaded) {
            Thread printer = new Thread(() -> {
                while (!Thread.interrupted()) {
                    try {
                        System.out.print("\rLoading to RAM.");
                        Thread.sleep(300);
                        System.out.print("\rLoading to RAM..");
                        Thread.sleep(300);
                        System.out.print("\rLoading to RAM...");
                        Thread.sleep(300);
                        System.out.print("\rLoading to RAM....");
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        logger.info("LoadPrinter thread interrupted.");
                        return;
                    }
                }
            }, "LoadPrinter");

            printer.start();

            try {
                List<String> criteria = new ArrayList<>();

                for (WalkMeCategory e : WalkMeCategory.getAll()) {
                    criteria.add(String.valueOf(e.id()));
                }

                List<Place> listPlaces = service.getAll(criteria, PlaceFields.CATEGORY_ID);

                for (Place p : listPlaces) {
                    nodeList.add(Node.of(p));
                }
            } catch (Exception e) {
                logger.error("Can't load to RAM. Message: " + e.getMessage());
                printer.interrupt();
            }

            printer.interrupt();
            System.out.println("OK. Place count: " + nodeList.size());
            isLoaded = true;
        }
    }

    public static List<Node> getAll() {
        return nodeList;
    }
}
