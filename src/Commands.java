public enum Commands {
    /* STABDYMAS */
    HALT, // stabdo
    /* ARITMETINES */
    DA, // R = R + D
    SB, // R = R - D
    ADxy, // R = R + [16 * x + y]
    BSxy, // R = R - [16 * x + y]
    /* PALYGINIMO */
    CR, // R > D
    CD, // R == D
    /* VALDYMO PERDAVIMO */
    JPxy, // persokti i adresa IC = 16 * x + y
    JCxy, // jei C = T, sokti i adresa IC = 16 * x + y
    /* DARBAS SU ATMINTIMI */
    LRxy, // R = [16 * x + y]
    LDxy, // D = [16 * x + y]
    SRxy, // [16 * x + y] = R
    SDxy, // [16 * x + y] = D
    /* IVEDIMAS IR ISVEDIMAS */
    GDxy, // Get data
    PDxy, // Put data
    SIxy, // Is ivedimo issaugoti skaiciu i [16 * x + y]
    PIxy // Atspausdinti skaiciu esanti [16 * x + y]
}
