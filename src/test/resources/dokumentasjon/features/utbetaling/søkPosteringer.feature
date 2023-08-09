# language: no
# encoding: UTF-8


Egenskap: Søk posteringer

  Scenario: Navn på rettighetshaver skal ikke vises når det ikke er søkt på rettighetshaver.
    Gitt en saksbehandler med følgende roller:
      | Rolle |
      | Les?? |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker    | Mottaker navn | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | 24417337179 | Sorte Bill    | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn       |
      | 24417337179 | Ugradert           | Sorte Bill |

    Når posteringer søkes etter med følgende kriterier:
      | Periodetype        | Periode FOM | Periode TOM | Posteringskonto fra | Posteringskonto til |
      | Utbetalingsperiode | 2022-12-01  | 2022-12-31  | 0000000             | 9999999             |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Rettighetshaver navn | Bilagsnummer | Posteringsbeløp | Mottaker    | Mottaker navn | Utbetalingsdato |
      | 24417337179     |                      | 733740504    | -3572,00        | 24417337179 |               | 2022-12-12      |


  Scenario: Når det ikke er søkt på mottaker skal navnet på mottakeren bare vises hvis mottaker er en organisasjon.

    Gitt en saksbehandler med følgende roller:
      | Rolle |
      | Les?? |

    Og at følgende posteringer finnes i UR:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker type | Mottaker    | Mottaker navn             | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | Person        | 24417337179 | Sorte Bill                | 2022-12-12      |
      | 24417337179     | 733740504    | -3572,00        | Organisasjon  | 889640782   | Arbeids-og velferdsetaten | 2022-12-12      |

    Og at følgende personer finnes i PDL:
      | Ident       | Adressebeskyttelse | Navn       |
      | 24417337179 | Ugradert           | Sorte Bill |

    Når posteringer søkes etter med følgende kriterier:
      | Periodetype        | Periode FOM | Periode TOM | Posteringskonto fra | Posteringskonto til |
      | Utbetalingsperiode | 2022-12-01  | 2022-12-31  | 0000000             | 9999999             |

    Så skal følgende posteringer returneres:
      | Rettighetshaver | Bilagsnummer | Posteringsbeløp | Mottaker type | Mottaker    | Mottaker navn             | Utbetalingsdato |
      | 24417337179     | 733740504    | -3572,00        | Person        | 24417337179 |                           | 2022-12-12      |
      | 24417337179     | 733740504    | -3572,00        | Organisasjon  | 889640782   | Arbeids-og velferdsetaten | 2022-12-12      |





