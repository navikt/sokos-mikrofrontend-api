# language: no
# encoding: UTF-8


Egenskap: Søk posteringer

  Scenario: Saksbehandler med vanlig leserolle skal kunne søke opp posteringer for personer som ikke har adressebeskyttelse.

    Gitt en saksbehandler med følgende roller:
      | Rolle |
      | Les?? |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn     |
      | 24417337179 | Ugradert           | Kon Kurs |

    Når posteringer søkes etter med følgende kriterier:
      | Rettighetshaver | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179     | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Navn     | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | Kon Kurs | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |


  Scenario: Saksbehandler med vanlig leserolle skal ikke kunne søke opp posteringer for rettighetshavere med adressebeskyttelse.

    Gitt en saksbehandler med følgende roller:
      | Rolle |
      | Les?? |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn     |
      | 24417337179 | FORTROLIG          | Kon Kurs |

    Når posteringer søkes etter med følgende kriterier:
      | Rettighetshaver | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179     | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende feilmelding gis: "Du har ikke tilgang til å søke opp denne personen."


  Scenario: Saksbehandler med vanlig leserolle skal ikke kunne søke opp posteringer for mottakere med adressebeskyttelse.

    Gitt en saksbehandler med følgende roller:
      | Rolle |
      | Les?? |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn     |
      | 24417337179 | FORTROLIG          | Kon Kurs |

    Når posteringer søkes etter med følgende kriterier:
      | Mottaker    | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179 | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende feilmelding gis: "Du har ikke tilgang til å søke opp denne personen."


  Scenario: Saksbehandler med fortrolig rolle skal kunne søke opp posteringer for rettighetshavere som er fortrolige

    Gitt en saksbehandler med følgende roller:
      | Rolle                     |
      | 0000-GA-okonomi-fortrolig |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn     |
      | 24417337179 | FORTROLIG          | Kon Kurs |

    Når posteringer søkes etter med følgende kriterier:
      | Rettighetshaver | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179     | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Navn     | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | Kon Kurs | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |


  Scenario: Saksbehandler med fortrolig rolle skal kunne søke opp posteringer for mottakere som er fortrolige

    Gitt en saksbehandler med følgende roller:
      | Rolle                     |
      | 0000-GA-okonomi-fortrolig |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn     |
      | 24417337179 | FORTROLIG          | Kon Kurs |

    Når posteringer søkes etter med følgende kriterier:
      | Mottaker    | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179 | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Navn | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     |      | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |


  Scenario: Saksbehandler med strengt fortrolig rolle skal kunne søke opp posteringer for personer som er strengt fortrolige

    Gitt en saksbehandler med følgende roller:
      | Rolle                             |
      | 0000-GA-okonomi-strengt_fortrolig |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn     |
      | 24417337179 | STRENGT_FORTROLIG  | Kon Kurs |

    Når posteringer søkes etter med følgende kriterier:
      | Rettighetshaver | Periodetype        | Periode FOM | Periode TOM |
      | 24417337179     | Utbetalingsperiode | 2022-12-01  | 2022-12-31  |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Navn     | Bilagsnummer | Posteringsbeløp | Mottaker    | Utbetalingsdato |
      | 24417337179     | Kon Kurs | 733740504    | -3572,00        | 24417337179 | 2022-12-12      |




