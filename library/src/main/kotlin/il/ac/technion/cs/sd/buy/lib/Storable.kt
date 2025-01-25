package il.ac.technion.cs.sd.lib

interface Storable{

}

interface StorableMarket : Storable {
    /**
    ** returns a map that maps productIDs to the quantity of  purchased items of them- do not include canceled orders
     */
    fun getAllProducts():Map<String,String>


    /**
     ** returns a map that maps productIDs to how much they cost (how much one costs...)
     * consider that their can be more than definition for a product and so cost can be changed
     */
    fun getAllPrices():Map<String,String>
    /**
     * returns a map that maps user-ids to pair of first::status and second::list of their orders , possibly some of them canceled or modified
     * status can be canceled , modified and nmc ; nmc for not modified neither canceled-
     * if its hard to give the status here also its ok ... it will be in OrdersIDsWithStatus
     */
    fun getUserIDsWithOrders():Map<String,Pair<String,List<String>>>
    /**
     * *return map of all valid order ids , possibly canceled also ..
     * with their status : canceled , modified or nmc
     */
    fun getOrdersIDsWithStatus():Map<String,String>
    /**
     * returns a map that maps order-ids to a pair of : the product and the modifications ...
     * if the order was cancelled we can add -1 in the end
     *
     * check piazza @54 and @50 , they will give us some answers
     */
    fun getOrderIDsWithProducts():Map<String,Pair<String,List<String>>>


}

