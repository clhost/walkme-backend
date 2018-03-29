package mappers;

public class PlaceToNodeMapper {//implements Mapper<Node, Place> {
    /*@Override
    public Node map(Place place) {
        Map<Day, ScheduleTime> fromMap = place.getSchedule().getScheduleInfo();
        Map<Day, ScheduleTime> toMap = new HashMap<>();

        for (Map.Entry<Day, ScheduleTime> entry : fromMap.entrySet()) {
            Day day;
            switch (entry.getKey()) {
                case MONDAY:
                    day = Day.MONDAY;
                    break;
                case TUESDAY:
                    day = Day.TUESDAY;
                    break;
                case WEDNESDAY:
                    day = Day.WEDNESDAY;
                    break;
                case THURSDAY:
                    day = Day.THURSDAY;
                    break;
                case FRIDAY:
                    day = Day.FRIDAY;
                    break;
                case SATURDAY:
                    day = Day.SATURDAY;
                    break;
                case SUNDAY:
                    day = Day.SUNDAY;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }

            toMap.put(day, new ScheduleTime(entry.getValue().getStart(), entry.getValue().getFinish()));
        }

        Schedule schedule = new Schedule(toMap);
        return new Node(
                place.getId(),
                new Location(place.getLocation().getLat(), place.getLocation().getLng()),
                schedule);*/


    public static void main(String[] args) throws Exception {
        /*Mapper<Node, Place> mapper = new PlaceToNodeMapper();
        PlaceService placeService = new PlaceService();

        Place place1 = placeService.get
                ("5348552838479901_nehhg2p8p713845B56IG0GGGlszk9z26G6G433A4G5BA04HArgewB4979B3IG22G0J5CI5G4k4gyuvG45516384A7991H3J2H42");
        Place place2 = placeService.get
                ("5348552838480147_ep9zwip8p7136598532GGGGcxt4pbu26G6G43399G5357J51rgewB4979A8IG1I0G8G5I4GJ8yoiuvG455169396B2H1H48A6");

        System.out.println("Places:");
        System.out.println(place1);
        System.out.println(place2);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        String a = gson.toJson(place1, Place.class);
        //JsonElement element = gson.toJsonTree(place1, Place.class);
        System.out.println("===================");
        System.out.println(a);
        System.out.println("===================");

        List<Node> nodes = new ArrayList<>();
        nodes.add(mapper.map(place1));
        nodes.add(mapper.map(place2));

        System.out.println("\nAfter algorithm: ");
        Ways ways = new Ways(100, nodes, new Location(59.923062, 30.358118));
        System.out.println(ways.getWays());*/
    }
}
