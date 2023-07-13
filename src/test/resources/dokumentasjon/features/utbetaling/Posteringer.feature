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




