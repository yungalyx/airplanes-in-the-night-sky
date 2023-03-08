
class DateTime {

    private String string_format;
    
    public DateTime(String date){
        this.string_format=date;
    }
    String Get_printable_string() {
      String[] items = this.Get_String().split("-");
       String result_str = items[0]+"."+items[1]+"."+items[2]+" at "+items[3]+':'+items[4];
        return result_str;

    }

    /**
     * @return the string_format
     */
    public String Get_String() {
        return string_format;
    }
    
}
